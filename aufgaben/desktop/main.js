const { app, BrowserWindow, Tray, Menu, Notification, nativeImage, ipcMain, shell } = require('electron');
const path = require('path');

app.setAppUserModelId('de.projektaufgaben.app'); // nötig für Windows-Benachrichtigungen
if (!app.requestSingleInstanceLock()) app.quit();

let win = null, tray = null, quitting = false;
const startHidden = process.argv.includes('--hidden');

function showWindow() {
  if (!win) return;
  if (win.isMinimized()) win.restore();
  win.show(); win.focus();
}

function createWindow() {
  win = new BrowserWindow({
    width: 1000, height: 800, show: !startHidden, autoHideMenuBar: true, title: 'Projekt-Aufgaben',
    icon: path.join(__dirname, 'icon.png'),
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      backgroundThrottling: false,   // Zeitgeber laufen auch im Hintergrund weiter
      contextIsolation: true, nodeIntegration: false
    }
  });
  win.loadFile(path.join(__dirname, '..', 'index.html'));
  win.webContents.setWindowOpenHandler(({ url }) => { shell.openExternal(url); return { action: 'deny' }; });
  // Schließen = in den Infobereich minimieren, damit die Erinnerung weiterläuft
  win.on('close', e => { if (!quitting) { e.preventDefault(); win.hide(); } });
}

function autostartEnabled() { return app.getLoginItemSettings().openAtLogin; }
function setAutostart(on) {
  if (!app.isPackaged) return; // im Entwicklungsmodus nichts im System registrieren
  app.setLoginItemSettings({ openAtLogin: on, args: ['--hidden'] });
}

function createTray() {
  tray = new Tray(nativeImage.createFromPath(path.join(__dirname, 'icon.png')).resize({ width: 16, height: 16 }));
  tray.setToolTip('Projekt-Aufgaben');
  const build = () => Menu.buildFromTemplate([
    { label: 'Öffnen', click: showWindow },
    { label: 'Mit Windows starten', type: 'checkbox', checked: autostartEnabled(), enabled: app.isPackaged,
      click: item => { setAutostart(item.checked); } },
    { type: 'separator' },
    { label: 'Beenden', click: () => { quitting = true; app.quit(); } }
  ]);
  tray.setContextMenu(build());
  tray.on('click', showWindow);
  tray.on('right-click', () => tray.setContextMenu(build()));
}

ipcMain.on('notify', (_e, title, body) => {
  if (!Notification.isSupported()) return;
  const n = new Notification({ title, body });
  n.on('click', showWindow);
  n.show();
});

app.on('second-instance', showWindow);
app.whenReady().then(() => {
  createWindow(); createTray();
  if (app.isPackaged) {
    // Beim ersten Start Autostart aktivieren (abschaltbar im Tray-Menü)
    const fs = require('fs');
    const flag = path.join(app.getPath('userData'), 'autostart-set');
    if (!fs.existsSync(flag)) { setAutostart(true); fs.writeFileSync(flag, '1'); }
  }
});
app.on('before-quit', () => { quitting = true; });
app.on('window-all-closed', () => { /* im Infobereich weiterlaufen */ });
