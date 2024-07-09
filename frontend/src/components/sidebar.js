import React from 'react';
import { Link } from 'react-router-dom';
import '../resources/sidebar.css';

const Sidebar = ({ onLogout }) => {
    return (
        <div className="sidebar">
            <h2>Navigation</h2>
            <ul>
                <li><Link to="/home">Home</Link></li>
                <li>Create new note</li>
                <li>View notes</li>
                <li><button onClick={onLogout}>Logout</button></li>
            </ul>
        </div>
    );
};

export default Sidebar;