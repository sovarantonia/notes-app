import React from 'react';
import {Link} from 'react-router-dom';
import '../resources/sidebar.css';
import {useUser} from "./userContext";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDoorOpen} from "@fortawesome/free-solid-svg-icons";

const Sidebar = ({onLogout}) => {
    const {logout} = useUser();

    const handleLogout = () => {
        logout();
    };

    return (
        <div className="sidebar">
            <h2>Navigation</h2>
            <ul>
                <div className="sidebar-elements">
                    <li><Link to="/home" id="homePageElement">Home</Link></li>
                    <li><Link to="/create-note" id="createNoteElement">Create new note</Link></li>
                    <li><Link to="/view-notes" id="viewNotesElement">View notes </Link></li>
                    <li><Link to="/shares" id="sharedNotesElement">Shares</Link> </li>
                    <li><Link to="/friends" id="friendList">Friends</Link></li>
                    <li><Link to="/user-requests" id="userRequests">Manage requests</Link></li>
                    <li><Link to="/profile" id="profileElement">Profile</Link></li>
                </div>

                <li>
                    <button onClick={handleLogout} id="logoutButton"><FontAwesomeIcon icon={faDoorOpen}/>Logout</button>
                </li>
            </ul>
        </div>
    );
};

export default Sidebar;