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

// Review modal management
const reviewModal = document.getElementById('review-modal');
const closeReviewModalBtn = document.getElementById('close-review-modal');
const cancelReviewBtn = document.getElementById('cancel-review-btn');
const reviewProductIdInput = document.getElementById('review-product-id');
const reviewProductNameP = document.getElementById('review-product-name');
const reviewForm = document.getElementById('review-form');
const reviewRatingInput = document.getElementById('review-rating');

function openReviewModal(productId, productName) {
    reviewModal.style.display = 'flex';
    reviewProductIdInput.value = productId;
    reviewProductNameP.textContent = productName;
    // Reset form
    reviewForm.reset();
    reviewProductIdInput.value = productId; // Restore after reset
    reviewRatingInput.value = ''; // Reset rating
    // Reset stars
    const stars = document.querySelectorAll('.star-rating i');
    stars.forEach(star => {
        star.classList.remove('fa-solid');
        star.classList.add('fa-regular');
    });
}

function closeReviewModal() {
    reviewModal.style.display = 'none';
}

closeReviewModalBtn.addEventListener('click', closeReviewModal);
cancelReviewBtn.addEventListener('click', closeReviewModal);

// Close modal when clicking outside it
window.addEventListener('click', (event) => {
    if (event.target === reviewModal) {
        closeReviewModal();
    }
});

// Star rating system
const stars = document.querySelectorAll('.star-rating i');
let selectedRating = 0;

stars.forEach((star, index) => {
    // Hover effect
    star.addEventListener('mouseenter', () => {
        stars.forEach((s, i) => {
            if (i <= index) {
                s.classList.remove('fa-regular');
                s.classList.add('fa-solid');
            } else {
                s.classList.remove('fa-solid');
                s.classList.add('fa-regular');
            }
        });
    });

    // Click to select
    star.addEventListener('click', () => {
        selectedRating = parseInt(star.getAttribute('data-rating'));
        reviewRatingInput.value = selectedRating;
        
        // Keep stars filled up to selected rating
        stars.forEach((s, i) => {
            if (i < selectedRating) {
                s.classList.remove('fa-regular');
                s.classList.add('fa-solid');
            } else {
                s.classList.remove('fa-solid');
                s.classList.add('fa-regular');
            }
        });
    });
});

// Reset to selected rating on mouse leave
const starContainer = document.querySelector('.star-rating');
if (starContainer) {
    starContainer.addEventListener('mouseleave', () => {
        stars.forEach((s, i) => {
            if (i < selectedRating) {
                s.classList.remove('fa-regular');
                s.classList.add('fa-solid');
            } else {
                s.classList.remove('fa-solid');
                s.classList.add('fa-regular');
            }
        });
    });
}

// Validate form before submit
reviewForm.addEventListener('submit', (e) => {
    if (!reviewRatingInput.value) {
        e.preventDefault();
        alert('Por favor, selecciona una puntuación');
    }
});
