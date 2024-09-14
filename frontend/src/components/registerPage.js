import React, {useState} from 'react';
import Header from "./header";
import {register} from "./api";
import {useNavigate} from 'react-router-dom';
import '../resources/register-page.css';
import '../resources/header.css';
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

        if (!validateForm()) return;

        try {
            await register(firstName, lastName, email, password);
            alert('Registration successful! Please login with your credentials.');
            history('/login');
        } catch (error) {
            try {
                const parsedError = JSON.parse(error.message);
                setError(parsedError || 'Something went wrong.');
            } catch (parseError) {
                setError('An unexpected error occurred.');
            }
        }
    };

    const validateForm = () => {
        if (!firstName || !lastName || !email || !password || !confirmPassword) {
            setError('All fields are required.');
            return false;
        }
        if (!validateEmail(email)) {
            setError('Invalid email format.');
            return false;
        }
        if (password.length < 6) {
            setError('Password must be at least 6 characters long.');
            return false;
        }
        if (password !== confirmPassword) {
            setError('Passwords do not match.');
            return false;
        }
        return true;
    };

    const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    const handleBlur = (field) => {
        if (field === 'email' && !validateEmail(email)) {
            setError('Invalid email format');
        } else if (field === 'password' && password.length < 6) {
            setError('Password must be at least 6 characters long');
        } else if (field === 'confirmPassword' && password !== confirmPassword) {
            setError('Passwords do not match');
        } else {
            setError('');
        }
    };

    return (
        <div className="container">
            <Header/>
            <form onSubmit={handleSubmit} className="form">
                {error && <div className="error" aria-live="assertive">{error}</div>}
                <h2>Register</h2>
                <div className="form-group">
                    <label htmlFor="firstName">First Name:</label>
                    <input
                        type="text"
                        id="firstName"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
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
                        onChange={(e) => setLastName(e.target.value)}
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
                        onChange={(e) => setEmail(e.target.value)}
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
                        onChange={(e) => setPassword(e.target.value)}
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
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        onBlur={() => handleBlur('confirmPassword')}
                        placeholder="Confirm password"
                        required
                    />
                </div>
                <button type="submit">Register</button>
                <a onClick={() => history('/login')}>Already have an account? Login here</a>
            </form>
            <img src={sign_up} alt="Sign Up" className="signup-image"/>
        </div>
    );
};

export default RegisterPage;