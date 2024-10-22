import React, {useState} from 'react';
import {login} from './api';
import '../resources/login-page.css';
import '../resources/header.css'
import login_image from "../resources/login.svg";
import Header from './header';
import {useNavigate} from "react-router-dom";
import {useUser} from './userContext';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const {login: setUser} = useUser();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email || !password) {
            setError('Enter your email and password');
            return;
        }

        try {
            const response = await login(email, password);
            const {userInfo, tokenValue} = response;
            const {email: userEmail, id: id, firstName, lastName} = userInfo;

            setUser({email: userEmail, id: id, firstName, lastName}, tokenValue);
            navigate('/home');
        } catch (error) {
            setError('Login failed. Please check your credentials.');
        }
    };

    return (
        <div className={"container"}>
            <Header/>
            <form onSubmit={handleSubmit} className={"form"} id="loginForm">
                {error && <div className="error" id="errorMessage">{error}</div>}
                <h2>Login</h2>
                <input type="email" id="emailInput" value={email} onChange={(e) => setEmail(e.target.value)}
                       placeholder="Email" required/>
                <input type="password" id="passwordInput" value={password} onChange={(e) => setPassword(e.target.value)}
                       placeholder="Password" required/>
                <button type="submit" id="loginButton">Login</button>
                <a onClick={() => window.location.href = '/register'}>Don't have an account? Register now!</a>
            </form>
            <img src={login_image}/>
        </div>
    );
};

export default LoginPage;