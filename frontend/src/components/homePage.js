import React from "react";
import Sidebar from "./sidebar";
import '../resources/homepage.css';
const HomePage = ({ onLogout }) => {
    return (
        <div className="home-page">
            <Sidebar onLogout={onLogout} />
            <div className="main-content">
                <h2>Home Page</h2>
            </div>
        </div>
    );
};

export default HomePage;