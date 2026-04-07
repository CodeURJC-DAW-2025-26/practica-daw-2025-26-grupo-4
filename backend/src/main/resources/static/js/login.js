const loginTab = document.getElementById('tab-login');
const registerTab = document.getElementById('tab-register');
const loginCard = document.querySelector('.login-card');

// Function to switch to Login
loginTab.addEventListener('click', () => {
    loginCard.classList.remove('mode-register');
    loginCard.classList.add('mode-login');
});

// Function to switch to Register
registerTab.addEventListener('click', () => {
    loginCard.classList.remove('mode-login');
    loginCard.classList.add('mode-register');
});
