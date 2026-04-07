// DOM elements
const modal = document.getElementById('address-modal');
const modalTitle = document.getElementById('modal-title');
const addAddressBtn = document.getElementById('add-address-btn');
const editAddressBtn = document.getElementById('edit-address-btn');
const closeModalBtn = document.getElementById('close-modal');
const cancelBtn = document.getElementById('cancel-btn');

// Function to open the modal
function openModal(title) {
    modal.style.display = 'flex';
    modalTitle.textContent = title;
}

// Function to close the modal
function closeModal() {
    modal.style.display = 'none';
}

// Event listeners to open modal
if (addAddressBtn) {
    addAddressBtn.addEventListener('click', () => openModal('Añadir dirección de envío'));
}

if (editAddressBtn) {
    editAddressBtn.addEventListener('click', () => openModal('Editar dirección de envío'));
}

// Event listeners to close modal
closeModalBtn.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);

// Close modal when clicking outside
window.addEventListener('click', (event) => {
    if (event.target === modal) {
        closeModal();
    }
});

// Toggle password visibility
const togglePassIcons = document.querySelectorAll('.toggle-pass');
togglePassIcons.forEach(icon => {
    icon.addEventListener('click', function() {
        const input = this.previousElementSibling;
        const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
        input.setAttribute('type', type);
        
        this.classList.toggle('fa-eye-slash');
        this.classList.toggle('fa-eye');
    });
});

// Profile photo preview
const profileImageInput = document.getElementById('profileImageInput');
const savePhotoBtn = document.getElementById('save-photo-btn');
const profilePhotoPreview = document.getElementById('profile-photo-preview');
const profilePhotoPlaceholder = document.getElementById('profile-photo-placeholder');

if (profileImageInput) {
    profileImageInput.addEventListener('change', function () {
        const file = this.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function (e) {
            if (profilePhotoPlaceholder) {
                profilePhotoPlaceholder.style.display = 'none';
            }
            profilePhotoPreview.src = e.target.result;
            profilePhotoPreview.style.display = 'block';
            savePhotoBtn.style.display = 'inline-flex';
        };
        reader.readAsDataURL(file);
    });
}
