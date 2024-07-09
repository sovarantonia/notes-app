import React, { useState } from 'react';
import { login } from './api';
import '../resources/login-page.css';
import '../resources/header.css'
import login_image from "../resources/login.svg";
import Header from './header';
import {useNavigate} from "react-router-dom";

const LoginPage = ({ onLogin }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email || !password) {
            setError('Enter the credentials');
            return;
        }

        try {
            await onLogin(email, password);
            navigate("/home");
        } catch (error) {
            setError('Login failed. Please check your credentials.');
        }
    };

    return (
        <div className={"container"}>
            <Header/>
            <form onSubmit={handleSubmit} className={"form"}>
                {error && <div className="error">{error}</div>}
                <h2>Login</h2>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
                <button type="submit">Login</button>
                <a onClick={() => window.location.href='/register'}>Don't have an account? Register now!</a>
            </form>
            <img src={login_image}/>
        </div>
    );
};

export default LoginPage;