import React, { createContext, useContext, useState } from 'react';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    const login = (userInfo, token) => {
        setUser({ ...userInfo, token });
        sessionStorage.setItem('tokenValue', token);
        sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
    };

    const logout = () => {
        setUser(null);
        sessionStorage.removeItem('tokenValue');
        sessionStorage.removeItem('userInfo')
        window.location.href = '/login'
    };

    const update = (userInfo) => {
        setUser(userInfo)
        sessionStorage.setItem('userInfo', JSON.stringify(userInfo))
    }

    return (
        <UserContext.Provider value={{ user, login, logout, update }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => useContext(UserContext);