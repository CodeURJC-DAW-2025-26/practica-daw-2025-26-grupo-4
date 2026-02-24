// Order expand/collapse functionality with slide animation
document.addEventListener('DOMContentLoaded', function() {
    const expandButtons = document.querySelectorAll('.expand-btn');

    expandButtons.forEach(button => {
        button.addEventListener('click', function() {
            const orderCard = this.closest('.order-card');
            const orderDetails = orderCard.querySelector('.order-details');
            const icon = this.querySelector('i');

            if (orderCard.classList.contains('collapsed')) {
                // Expand
                orderCard.classList.remove('collapsed');
                orderDetails.style.maxHeight = orderDetails.scrollHeight + 'px';
                icon.classList.remove('fa-chevron-down');
                icon.classList.add('fa-chevron-up');
            } else {
                // Collapse
                orderCard.classList.add('collapsed');
                orderDetails.style.maxHeight = '0';
                icon.classList.remove('fa-chevron-up');
                icon.classList.add('fa-chevron-down');
            }
        });
    });
});
