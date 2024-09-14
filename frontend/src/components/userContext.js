import React, { createContext, useContext, useState } from 'react';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    const login = (userInfo, token) => {
        setUser({ ...userInfo, token });
        sessionStorage.setItem('tokenValue', token);
    };

    const logout = () => {
        setUser(null);
        sessionStorage.removeItem('tokenValue');
    };

    return (
        <UserContext.Provider value={{ user, login, logout }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => useContext(UserContext);