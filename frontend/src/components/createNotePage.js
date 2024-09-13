import Sidebar from "./sidebar";
import React from "react";
import { useState } from 'react';
import DatePicker from "react-datepicker";
import note from "./api";

import "react-datepicker/dist/react-datepicker.css";
import {useUser} from "./userContext";
const CreateNotePage = ({ onLogout }) => {
    const [error, setError] = useState('');

    const [title, setTitle] = useState('')
    const [text, setText] = useState('')
    const [date, setDate] = useState(new Date())
    const [grade, setGrade] = useState('')

    const { user } = useUser();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!title) {
            setError('Enter a title')
            return;
        }

        try {
            await note(user.userId, title, text, date, grade)
            alert('Note was created');
        }catch(error) {
            try {
                const parsedError = JSON.parse(error.message);
                setError(parsedError || 'Something went wrong.');
            } catch (parseError) {
                setError('An unexpected error occurred.');
            }
        }
    }
    return (
        <div className="create-note-page">
            <Sidebar onLogout={onLogout} />
            <div className="main-content">
                <h2>Create note</h2>
                <div className={"create-form"}>
                    <form onSubmit={handleSubmit} className={"form"}>
                        <input type="title" value={title} onChange={e => setTitle(e.target.value)} placeholder="Title" required/>
                        <input type="text" value={text} onChange={e => setText(e.target.value)} placeholder="Text" />
                        <DatePicker selected={date} onChange={(date) => setDate(date)} />
                        <input type="grade" value={grade} onChange={e => setGrade(e.target.value)} placeholder="Title" required/>
                        <button type="submit">Submit</button>
                    </form>
                </div>

            </div>
        </div>
    );
};
export default CreateNotePage;