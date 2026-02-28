// Order expand/collapse with event delegation (works for dynamically added cards too)
document.addEventListener('click', function(e) {
    const btn = e.target.closest('.expand-btn');
    if (!btn) return;

    const orderCard = btn.closest('.order-card');
    const orderDetails = orderCard.querySelector('.order-details');
    const icon = btn.querySelector('i');

    if (orderCard.classList.contains('collapsed')) {
        orderCard.classList.remove('collapsed');
        orderDetails.style.maxHeight = orderDetails.scrollHeight + 'px';
        icon.classList.remove('fa-chevron-down');
        icon.classList.add('fa-chevron-up');
    } else {
        orderCard.classList.add('collapsed');
        orderDetails.style.maxHeight = '0';
        icon.classList.remove('fa-chevron-up');
        icon.classList.add('fa-chevron-down');
    }
});
