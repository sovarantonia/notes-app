import React, {useState} from 'react';
import Header from "./header";
import {register} from "./api";
import {useNavigate} from 'react-router-dom';
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
        setError('');
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

        if (!firstName || !lastName || !email || !password || !confirmPassword) {
            setError('All fields are required')
            return;
        }

        try {
            await register(firstName, lastName, email, password);
            // Registration successful, display notification and redirect to login page
            alert('Registration successful! Please login with your credentials.');
            history('/login');
        } catch (error) {
            console.log(error.message);
            setError(JSON.parse(error.message));
        }
    };

    const handleBlur = (field) => {
        if (field === 'email' && !validateEmail(email) && email.length !== 0) {
            setError('Invalid email format');
            return;
        }
        if (field === 'password' && password.length < 6 && password.length !== 0) {
            setError('Password must be at least 6 characters long');
            return;
        }
        if (field === 'confirmPassword' && password !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        setError('');
    }

    const validateEmail = (email) => {
        // Basic email format validation
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    };

    return (
        <div className="container">
            <Header/>
            <form onSubmit={handleSubmit} className="form">
                {error && <div className="error">{error}</div>}
                <h2>Register</h2>
                <div className="form-group">
                    <label htmlFor="firstName">First Name:</label>
                    <input
                        type="text"
                        id="firstName"
                        value={firstName}
                        onChange={(e) => {
                            setFirstName(e.target.value);
                            setError('');
                        }}
                        onBlur={() => handleBlur('firstName')}
                        placeholder="First name"
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="lastName">Last Name:</label>
                    <input
                        type="text"
                        id="lastName"
                        value={lastName}
                        onChange={(e) => {
                            setLastName(e.target.value);
                            setError('');
                        }}
                        onBlur={() => handleBlur('lastName')}
                        placeholder="Last name"
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="email">Email:</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => {
                            setEmail(e.target.value);
                            setError('');
                        }}
                        onBlur={() => handleBlur('email')}
                        placeholder="Email"
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => {
                            setPassword(e.target.value);
                            setError('');
                        }}
                        onBlur={() => handleBlur('password')}
                        placeholder="Password"
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password:</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        value={confirmPassword}
                        onChange={(e) => {
                            setConfirmPassword(e.target.value);
                            setError('');
                        }}
                        onBlur={() => handleBlur('confirmPassword')}
                        placeholder="Confirm password"
                        required
                    />
                </div>
                <button type="submit">Register</button>
                <a onClick={() => window.location.href = '/login'}>Already have an account? Login here</a>
            </form>
            <img src={sign_up} alt="Sign Up" className="signup-image"/>
        </div>
    );
}

export default RegisterPage;