// Elementos del DOM
const modal = document.getElementById('address-modal');
const modalTitle = document.getElementById('modal-title');
const addAddressBtn = document.getElementById('add-address-btn');
const editAddressBtn = document.getElementById('edit-address-btn');
const closeModalBtn = document.getElementById('close-modal');
const cancelBtn = document.getElementById('cancel-btn');

// Función para abrir el modal
function openModal(title) {
    modal.style.display = 'flex';
    modalTitle.textContent = title;
}

// Función para cerrar el modal
function closeModal() {
    modal.style.display = 'none';
}

// Event Listeners para abrir modal
if (addAddressBtn) {
    addAddressBtn.addEventListener('click', () => openModal('Añadir dirección de envío'));
}

if (editAddressBtn) {
    editAddressBtn.addEventListener('click', () => openModal('Editar dirección de envío'));
}

// Event Listeners para cerrar modal
closeModalBtn.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);

// Cerrar modal al hacer clic fuera de él
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
