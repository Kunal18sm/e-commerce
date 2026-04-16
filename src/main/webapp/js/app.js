/**
 * ShopZone — Client-Side JavaScript
 * Handles UI interactions, toasts, modals, form validation, and cart operations.
 */

// ==================== TOAST NOTIFICATIONS ====================

const ToastManager = {
    container: null,

    init() {
        this.container = document.getElementById('toast-container');
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.id = 'toast-container';
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        }
    },

    show(message, type = 'success', duration = 3500) {
        if (!this.container) this.init();

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
        toast.innerHTML = `<span>${icons[type] || '📢'}</span><span>${message}</span>`;

        this.container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(40px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    success(msg) { this.show(msg, 'success'); },
    error(msg) { this.show(msg, 'error'); },
    warning(msg) { this.show(msg, 'warning'); },
    info(msg) { this.show(msg, 'info'); }
};

// ==================== MODAL MANAGEMENT ====================

const ModalManager = {
    open(modalId) {
        const overlay = document.getElementById(modalId);
        if (overlay) {
            overlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    },

    close(modalId) {
        const overlay = document.getElementById(modalId);
        if (overlay) {
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    },

    init() {
        // Close modal on overlay click
        document.querySelectorAll('.modal-overlay').forEach(overlay => {
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) {
                    overlay.classList.remove('active');
                    document.body.style.overflow = '';
                }
            });
        });

        // Close modal on close button click
        document.querySelectorAll('.modal-close').forEach(btn => {
            btn.addEventListener('click', () => {
                const overlay = btn.closest('.modal-overlay');
                if (overlay) {
                    overlay.classList.remove('active');
                    document.body.style.overflow = '';
                }
            });
        });

        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                document.querySelectorAll('.modal-overlay.active').forEach(overlay => {
                    overlay.classList.remove('active');
                });
                document.body.style.overflow = '';
            }
        });
    }
};

// ==================== FORM VALIDATION ====================

const FormValidator = {
    validate(formId) {
        const form = document.getElementById(formId);
        if (!form) return true;

        let isValid = true;
        const requiredFields = form.querySelectorAll('[required]');

        requiredFields.forEach(field => {
            this.clearError(field);
            if (!field.value.trim()) {
                this.showError(field, 'This field is required');
                isValid = false;
            }
        });

        // Password match validation
        const password = form.querySelector('[name="password"]');
        const confirm = form.querySelector('[name="confirmPassword"]');
        if (password && confirm && password.value !== confirm.value) {
            this.showError(confirm, 'Passwords do not match');
            isValid = false;
        }

        return isValid;
    },

    showError(field, message) {
        field.style.borderColor = '#EF4444';
        let errorEl = field.nextElementSibling;
        if (!errorEl || !errorEl.classList.contains('field-error')) {
            errorEl = document.createElement('span');
            errorEl.className = 'field-error';
            errorEl.style.cssText = 'color: #EF4444; font-size: 0.8rem; margin-top: 4px; display: block;';
            field.parentNode.insertBefore(errorEl, field.nextSibling);
        }
        errorEl.textContent = message;
    },

    clearError(field) {
        field.style.borderColor = '';
        const errorEl = field.nextElementSibling;
        if (errorEl && errorEl.classList.contains('field-error')) {
            errorEl.remove();
        }
    }
};

// ==================== CART OPERATIONS ====================

function addToCart(productId) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/cart';
    form.innerHTML = `
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="productId" value="${productId}">
        <input type="hidden" name="quantity" value="1">
    `;
    document.body.appendChild(form);
    form.submit();
}

function removeFromCart(productId) {
    if (!confirm('Remove this item from your cart?')) return;
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/cart';
    form.innerHTML = `
        <input type="hidden" name="action" value="remove">
        <input type="hidden" name="productId" value="${productId}">
    `;
    document.body.appendChild(form);
    form.submit();
}

function updateQuantity(productId, newQty) {
    if (newQty < 1) {
        removeFromCart(productId);
        return;
    }
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = getContextPath() + '/cart';
    form.innerHTML = `
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="productId" value="${productId}">
        <input type="hidden" name="quantity" value="${newQty}">
    `;
    document.body.appendChild(form);
    form.submit();
}

// ==================== DELETE CONFIRMATION ====================

function confirmDelete(message, formId) {
    if (confirm(message || 'Are you sure you want to delete this?')) {
        document.getElementById(formId).submit();
    }
}

function submitForm(formId) {
    document.getElementById(formId).submit();
}

// ==================== SEARCH ====================

function handleSearch(event, baseUrl) {
    if (event.key === 'Enter') {
        const query = event.target.value.trim();
        if (query) {
            window.location.href = baseUrl + '?q=' + encodeURIComponent(query);
        } else {
            window.location.href = baseUrl;
        }
    }
}

// ==================== UTILITIES ====================

function getContextPath() {
    const path = window.location.pathname;
    const idx = path.indexOf('/', 1);
    return idx > 0 ? path.substring(0, idx) : '';
}

// ==================== URL PARAM DETECTION ====================

function checkUrlMessages() {
    const params = new URLSearchParams(window.location.search);

    if (params.get('msg') === 'placed') {
        ToastManager.success('Order placed successfully! You can track it in your order history.');
    } else if (params.get('msg') === 'added') {
        ToastManager.success('Item added successfully!');
    } else if (params.get('msg') === 'deleted') {
        ToastManager.info('Item deleted successfully.');
    } else if (params.get('msg') === 'updated') {
        ToastManager.success('Updated successfully!');
    } else if (params.get('msg') === 'loggedout') {
        ToastManager.info('You have been logged out.');
    } else if (params.get('error') === 'unauthorized') {
        ToastManager.error('Access denied. You do not have permission.');
    }
}

// ==================== INITIALIZATION ====================

document.addEventListener('DOMContentLoaded', function() {
    ToastManager.init();
    ModalManager.init();
    checkUrlMessages();

    // Add smooth animation to product cards
    const cards = document.querySelectorAll('.product-card, .stat-card, .order-card');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                entry.target.style.animationDelay = `${index * 0.05}s`;
                entry.target.classList.add('animate-scaleIn');
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    cards.forEach(card => observer.observe(card));
});
