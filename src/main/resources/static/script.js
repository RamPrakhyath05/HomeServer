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

// File type helpers
function getIcon(filename) {
    const ext = filename.split('.').pop().toLowerCase();
    if (['jpg','jpeg','png','gif','webp','svg'].includes(ext)) return '🖼';
    if (['mp4','webm','mov','mkv'].includes(ext)) return '🎬';
    if (['mp3','wav','ogg','flac'].includes(ext)) return '🎵';
    if (['pdf'].includes(ext)) return '📕';
    if (['txt','md','log'].includes(ext)) return '📝';
    if (['js','ts','java','py','sh','css','html','json','yml','yaml','xml'].includes(ext)) return '💻';
    if (['zip','tar','gz','rar'].includes(ext)) return '📦';
    return '📄';
}

function getType(filename) {
    const ext = filename.split('.').pop().toLowerCase();
    if (['jpg','jpeg','png','gif','webp','svg'].includes(ext)) return 'image';
    if (['mp4','webm','mov'].includes(ext)) return 'video';
    if (['pdf'].includes(ext)) return 'pdf';
    if (['txt','md','log','js','ts','java','py','sh','css','html','json','yml','yaml','xml'].includes(ext)) return 'text';
    return 'unsupported';
}

// Preview Pane
async function previewFile(filename) {
    // Mark active
    document.querySelectorAll('.file-item').forEach(el => el.classList.remove('active'));
    document.getElementById(`file-${CSS.escape(filename)}`).classList.add('active');

    const pane = document.getElementById('preview-pane');
    const type = getType(filename);
    const url = `/files/preview/${encodeURIComponent(filename)}`;

    let content = '';

    if (type === 'image') {
        content = `<img src="${url}" alt="${filename}">`;
    } else if (type === 'video') {
        content = `<video controls><source src="${url}">Your browser does not support video.</video>`;
    } else if (type === 'pdf') {
        content = `<iframe src="${url}"></iframe>`;
    } else if (type === 'text') {
        try {
            const res = await fetch(url);
            const text = await res.text();
            const escaped = text.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
            content = `<pre>${escaped}</pre>`;
        } catch (e) {
            content = `<div class="unsupported">Failed to load file</div>`;
        }
    } else {
        content = `
            <div class="unsupported">
                <div style="font-size:2rem">📦</div>
                <p>Preview not available for this file type</p>
                <a class="download-btn" href="${url}" download="${filename}">↓ download instead</a>
            </div>`;
    }

    pane.innerHTML = `
        <div class="preview-header">
            <span class="preview-filename">${getIcon(filename)} ${filename}</span>
            <a class="download-btn" href="${url}" download="${filename}">↓ download</a>
        </div>
        <div class="preview-body">${content}</div>
    `;
}

// Load files
async function loadFiles() {
    try {
        const res = await fetch('/files');
        const files = await res.json();
        const list = document.getElementById('file-list');

        if (files.length === 0) {
            list.innerHTML = '<div class="empty-state">No files yet. Upload something!</div>';
            return;
        }

        list.innerHTML = files.map(f => `
            <div class="file-item" id="file-${CSS.escape(f)}" onclick="previewFile('${f}')">
                <div class="file-name">
                    <span class="file-icon">${getIcon(f)}</span>
                    ${f}
                </div>
                <div class="file-actions">
                    <a class="download-btn" href="/files/${f}" download="${f}" onclick="event.stopPropagation()">↓</a>
                    <button class="delete-btn" onclick="event.stopPropagation(); deleteFile('${f}')">🗑</button>
                </div>
            </div>
        `).join('');
    } catch (e) {
        showToast('Failed to load files', 'error');
    }
}

// Upload with progress
function uploadFile(file) {
    if (!file) return;
    const form = new FormData();
    form.append('file', file);

    const xhr = new XMLHttpRequest();

    xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) {
            const percent = Math.round((e.loaded / e.total) * 100);
            showToast(`Uploading... ${percent}%`);
        }
    });

    xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
            showToast(`${file.name} uploaded!`);
            loadFiles();
        } else {
            showToast('Upload failed', 'error');
        }
    });

    xhr.addEventListener('error', () => showToast('Upload failed', 'error'));
    xhr.open('POST', '/files/upload');
    xhr.send(form);
}

// Delete
async function deleteFile(filename) {
    try {
        const res = await fetch(`/files/${filename}`, { method: 'DELETE' });
        if (res.ok) {
            showToast(`${filename} deleted!`);
            document.getElementById('preview-pane').innerHTML = `
                <div class="preview-empty">
                    <div style="font-size: 2rem;">👁</div>
                    <p>Click a file to preview</p>
                </div>`;
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
