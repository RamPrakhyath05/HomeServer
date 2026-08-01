// Theme
function toggleTheme() {
    const html = document.documentElement;
    const isDark = html.getAttribute('data-theme') === 'dark';
    html.setAttribute('data-theme', isDark ? 'light' : 'dark');
    document.querySelector('.theme-toggle').textContent = isDark ? '🌙 dark' : '☀ light';
}

// Toast
function showToast(msg, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.className = `toast ${type} show`;
    setTimeout(() => toast.classList.remove('show'), 3000);
}

// Load files
async function loadFiles() {
    try {
        const res = await fetch(`/files`);
        const files = await res.json();
        const list = document.getElementById('file-list');

        if (files.length === 0) {
            list.innerHTML = '<div class="empty-state">No files yet. Upload something!</div>';
            return;
        }

        list.innerHTML = files.map(f => `
            <div class="file-item">
                <div class="file-name">
                    <span class="file-icon">📄</span>
                    ${f}
                </div>
                <div class="file-actions">
                    <a class="download-btn" href="/files/${f}" download="${f}">↓ download</a>
                </div>
            </div>
        `).join('');
        list.innerHTML = files.map(f => `
            <div class="file-item">
                <div class="file-name">
                    <span class="file-icon">📄</span>
                    ${f}
                </div>
                <div class="file-actions">
                    <a class="download-btn" href="/files/${f}" download="${f}">↓ download</a>
                    <button class="delete-btn" onclick="deleteFile('${f}')">🗑</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        showToast('Failed to load files', 'error');
    }
}

// Upload
async function uploadFile(file) {
    if (!file) return;
    const form = new FormData();
    form.append('file', file);
    try {
        const res = await fetch(`/files/upload`, { method: 'POST', body: form });
        if (res.ok) {
            showToast(`${file.name} uploaded!`);
            loadFiles();
        } else {
            showToast('Upload failed', 'error');
        }
    } catch (e) {
        showToast('Upload failed', 'error');
    }
}

// Delete
async function deleteFile(filename) {
    try {
        const res = await fetch(`/files/${filename}`, { method: 'DELETE' });
        if (res.ok) {
            showToast(`${filename} deleted!`);
            loadFiles();
        } else {
            showToast('Delete failed', 'error');
        }
    } catch (e) {
        showToast('Delete failed', 'error');
    }
}

// Drag and drop
const zone = document.getElementById('upload-zone');
zone.addEventListener('dragover', e => { e.preventDefault(); zone.classList.add('dragover'); });
zone.addEventListener('dragleave', () => zone.classList.remove('dragover'));
zone.addEventListener('drop', e => {
    e.preventDefault();
    zone.classList.remove('dragover');
    uploadFile(e.dataTransfer.files[0]);
});

loadFiles();

