import React, { useState } from 'react';
import { login } from './api';
import { useNavigate } from 'react-router-dom';
import './login-register-header.css';
import login_image from "../resources/login.svg";
import Header from './header';
const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loggedIn, setLoggedIn] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email || !password){
            setError('Enter the credentials')
        }
        try {
            await login(email, password);
            setLoggedIn(true);
        } catch (error) {
            setError('Login failed. Please check your credentials.');
        }
    };

    if (loggedIn) {
        alert("Successful login");
    }

    return (
        <div className={"container"}>
            <Header/>
            <h2>Login</h2>
            <form onSubmit={handleSubmit} className={"form"}>
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