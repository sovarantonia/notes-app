import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const login = async (email, password) => {
    try {
        const response = await axios.post(`${API_URL}/login`, {email, password});
        return response.data;
    } catch (error) {
        throw error.response.data.message;
    }
};

export const register = async (firstName, lastName, email, password) => {
    try {
        const response = await axios.post(`${API_URL}/register`, {firstName, lastName, email, password});
        return response.data;
    } catch (error) {
        throw error.response.data.message;

    }
};
