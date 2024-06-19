import React, {useState} from 'react';
import Header from "./header";
import {register} from "./api";
import { useNavigate } from 'react-router-dom';
import './register-page.css';
import './header.css';
import sign_up from '../resources/sign_up.svg';
const RegisterPage = () => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const history = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        if (!validateEmail(email)) {
            setError('Invalid email format');
            return;
        }
        if (password.length < 6) {
            setError('Password must be at least 6 characters long');
            return;
        }

        if(!firstName || !lastName || !email || !password || !confirmPassword){
            setError('All fields are required')
        }

        try {
            await register(firstName, lastName, email, password);
            // Registration successful, display notification and redirect to login page
            alert('Registration successful! Please login with your credentials.');
            history('/login');
        } catch (error) {
            setError(error);
        }
    };

    const validateEmail = (email) => {
        // Basic email format validation
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    };

    return (
        <div className={"container"}>
            <Header/>
            <form onSubmit={handleSubmit} className={"form"}>
                <h2>Register</h2>
                <input type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)}
                       placeholder="First Name" required/>
                <input type="text" value={lastName} onChange={(e) => setLastName(e.target.value)}
                       placeholder="Last Name" required/>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email"
                       required/>
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                       placeholder="Password" required/>
                <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)}
                       placeholder="Confirm Password" required/>
                <button type="submit">Register</button>
                <a onClick={() => window.location.href='/login'}>Already have an account? Login here</a>
            </form>
            <img src={sign_up}/>
        </div>
    );
}

export default RegisterPage;