const { contextBridge, ipcRenderer } = require('electron');
contextBridge.exposeInMainWorld('desktopNotify', (title, body) => ipcRenderer.send('notify', String(title), String(body)));
