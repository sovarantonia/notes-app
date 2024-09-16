import React from 'react';
import { Link } from 'react-router-dom';
import '../resources/sidebar.css';
import {useUser} from "./userContext";

const Sidebar = ({ onLogout }) => {
    const { logout } = useUser();

    const handleLogout = () => {
        logout();
    };

    return (
        <div className="sidebar">
            <h2>Navigation</h2>
            <ul>
                <li><Link to="/home">Home</Link></li>
                <li><Link to="/create-note">Create new note</Link></li>
                <li>View notes</li>
                <li><Link to="/profile">Profile</Link></li>
                <li><button onClick={handleLogout}>Logout</button></li>
            </ul>
        </div>
    );
};

export default Sidebar;