const userMenuBtn = document.getElementById('userMenuBtn');
const userMenuDropdown = document.getElementById('userMenuDropdown');

userMenuBtn.addEventListener('click', function(e) {
    e.stopPropagation();
    userMenuDropdown.classList.toggle('active');
});

document.addEventListener('click', function(e) {
    if (!e.target.closest('.user-menu-container')) {
        userMenuDropdown.classList.remove('active');
    }
});
