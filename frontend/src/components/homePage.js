import React from "react";
import Sidebar from "./sidebar";
import '../resources/homepage.css';
import {useUser} from "./userContext";
const HomePage = () => {
    const { logout } = useUser();

    const handleLogout = () => {
        logout();
    };
    return (
        <div className="home-page">
            <Sidebar onLogout={handleLogout}/>
            <div className="main-content">
                <h2>Home Page</h2>
            </div>
        </div>
    );
};

export default HomePage;