import React from 'react';

import '../resources/delete-popup.css'
const ConfirmDeletePopup = ({ isOpen, onClose, onConfirm }) => {
    if (!isOpen) return null;

    return (
        <div className="popup-overlay">
            <div className="popup-content">
                <h2>Confirm Account Deletion</h2>
                <p>Are you sure you want to delete your account? This action cannot be undone.</p>
                <div className="popup-actions">
                    <button type="delete" onClick={onConfirm}>Yes, Delete</button>
                    <button type="cancel" onClick={onClose}>Cancel</button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmDeletePopup;