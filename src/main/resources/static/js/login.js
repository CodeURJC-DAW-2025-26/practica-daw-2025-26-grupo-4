const loginTab = document.getElementById('tab-login');
const registerTab = document.getElementById('tab-register');
const loginCard = document.querySelector('.login-card');

// Función para cambiar a Login
loginTab.addEventListener('click', () => {
    loginCard.classList.remove('mode-register');
    loginCard.classList.add('mode-login');
});

// Función para cambiar a Registro
registerTab.addEventListener('click', () => {
    loginCard.classList.remove('mode-login');
    loginCard.classList.add('mode-register');
});
