import React from 'react';
import { Link } from 'react-router-dom';
import '../resources/sidebar.css';
import {useUser} from "./userContext";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDoorOpen} from "@fortawesome/free-solid-svg-icons";

const Sidebar = ({ onLogout }) => {
    const { logout } = useUser();

    const handleLogout = () => {
        logout();
    };

    return (
        <div className="sidebar">
            <h2>Navigation</h2>
            <ul>
                <div className="sidebar-elements"><li><Link to="/home">Home</Link></li>
                    <li><Link to="/create-note">Create new note</Link></li>
                    <li><Link to="/view-notes">View notes </Link></li>
                    <li><Link to="/profile">Profile</Link></li>
                </div>

                <li><button onClick={handleLogout}><FontAwesomeIcon icon={faDoorOpen} />Logout</button></li>
            </ul>
        </div>
    );
};

export default Sidebar;