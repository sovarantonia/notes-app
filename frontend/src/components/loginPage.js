import React, { useState } from 'react';
import { Redirect } from 'react-router-dom';
import { login } from './api';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loggedIn, setLoggedIn] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login(email, password);
            setLoggedIn(true);
        } catch (error) {
            setError('Login failed. Please check your credentials.');
        }
    };

    if (loggedIn) {
        return <Redirect to="/success" />;
    }

    return (
        <div>
            <h2>Login</h2>
            {error && <div>{error}</div>}
            <form onSubmit={handleSubmit}>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
                <button type="submit">Login</button>
            </form>
        </div>
    );
};

export default LoginPage;