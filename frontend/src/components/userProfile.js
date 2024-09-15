import {useUser} from "./userContext";
import Sidebar from "./sidebar";
import React from "react";
import {useState} from 'react';
import {updateUserCredentials} from "./api";

const UserProfile = () => {
    const [error, setError] = useState('');
    const {logout, update} = useUser();

    const user = JSON.parse(sessionStorage.getItem('userInfo')) || {};
    const userId = user.id || null;

    const [firstName, setFirstName] = useState(user.firstName || '');
    const [lastName, setLastName] = useState(user.lastName || '');

    const handleLogout = () => {
        logout();
        window.location.href = '/login';
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        if (!firstName || !lastName) {
            setError('Fields should not be empty');
            return;
        }
        try {
            const response = await updateUserCredentials(userId, firstName, lastName);

            const updatedUser = {...user, firstName: response.firstName, lastName: response.lastName};
            sessionStorage.setItem('userInfo', JSON.stringify(updatedUser));

            update(updatedUser)

            alert('Profile updated successfully');
        } catch (error) {
            setError('Error updating profile');
        }
    };

    return (
        <div className="user-profile">
            <Sidebar onLogout={handleLogout}/>
            <div className="main-content">
                <form onSubmit={handleSubmit} className="form">
                    {error && <div className="error" aria-live="assertive">{error}</div>}
                    <h2>User profile</h2>
                    <div className="form-group">
                        <label htmlFor="firstName">First Name:</label>
                        <input
                            type="text"
                            id="firstName"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            placeholder="First name"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="lastName">Last Name:</label>
                        <input
                            type="text"
                            id="lastName"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            placeholder="Last name"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">Email:</label>
                        <input
                            type="email"
                            id="email"
                            value={user.email || ''}
                            readOnly
                            placeholder="Email"
                            required
                        />
                    </div>
                    <button type="submit">Update</button>
                </form>
            </div>
        </div>
    );
};

export default UserProfile;