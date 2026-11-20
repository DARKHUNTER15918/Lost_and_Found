/**
 * Campus Lost & Found Management System - Main JavaScript
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Auto dismiss alert messages after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 6000);
    });

    // 2. Real-time Image Preview for Item Report Forms
    const imageInput = document.getElementById('imageFile');
    const previewContainer = document.getElementById('imagePreviewContainer');
    const previewImage = document.getElementById('imagePreview');

    if (imageInput && previewContainer && previewImage) {
        imageInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (file) {
                // Check file size (max 10MB)
                if (file.size > 10 * 1024 * 1024) {
                    alert('File size exceeds 10MB limit. Please choose a smaller image.');
                    imageInput.value = '';
                    previewContainer.classList.add('d-none');
                    return;
                }

                const reader = new FileReader();
                reader.onload = function (event) {
                    previewImage.src = event.target.result;
                    previewContainer.classList.remove('d-none');
                };
                reader.readAsDataURL(file);
            } else {
                previewContainer.classList.add('d-none');
            }
        });
    }

    // 3. Delete Confirmation Dialogs
    const deleteForms = document.querySelectorAll('form.confirm-delete');
    deleteForms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const itemName = form.getAttribute('data-item-name') || 'this item';
            if (!confirm(`Are you sure you want to delete ${itemName}? This action cannot be undone.`)) {
                e.preventDefault();
            }
        });
    });
});
