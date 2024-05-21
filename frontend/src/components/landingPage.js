import React from 'react';
import {Link} from 'react-router-dom';
import Header from "./header";

const LandingPage = () => {
    return (
        <div>
            <Header/>
            <h1>Welcome to Our App</h1>
            <Link to="/login">Login</Link>
            <br/>
            <Link to="/register">Register</Link>
        </div>
    );
}

export default LandingPage;