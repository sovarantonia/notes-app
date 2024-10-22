import React, {useEffect, useState} from "react";
import {getNoteById, updateNote} from "./api";
import {
    Button,
    Dialog,
    FormControl,
    InputLabel,
    MenuItem, Select,
    TextField
} from "@mui/material";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSave, faTimes} from "@fortawesome/free-solid-svg-icons";
import '../resources/update-note-dialog.css';

const UpdateNoteDialog = ({open, onClose, noteId, onUpdate}) => {
    const [note, setNote] = useState(null);
    const [grade, setGrade] = useState('');
    const [options] = useState([
        {label: '1', value: 1},
        {label: '2', value: 2},
        {label: '3', value: 3},
        {label: '4', value: 4},
        {label: '5', value: 5},
        {label: '6', value: 6},
        {label: '7', value: 7},
        {label: '8', value: 8},
        {label: '9', value: 9},
        {label: '10', value: 10},
    ]);
    const [error, setError] = useState('');

    useEffect(() => {
        if (open && noteId) {
            const fetchNote = async () => {
                try {
                    const data = await getNoteById(noteId);
                    setNote(data);
                    setGrade(data.grade);
                } catch (err) {
                    setError('Failed to fetch note');
                }
            };

            fetchNote();
        }
    }, [open, noteId]);

    const handleGradeChange = (event) => {
        setGrade(event.target.value);
    };

    const handleUpdate = async () => {
        if (note) {
            try {
                await updateNote(noteId,
                    note.userId,
                    note.title,
                    note.text,
                    note.date,
                    grade,
                );
                setError('')
                onUpdate();
                onClose();
            } catch (err) {
                setError('Failed to update note');
            }
        }
    };

    if (!note) return null;

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" classname="update-dialog">
            <div className="dialog-content">
                <h2>Edit Note</h2>
                {error && <div className="error">{error}</div>}
                <TextField
                    label="Title"
                    value={note.title}
                    onChange={(e) => setNote({...note, title: e.target.value})}
                    fullWidth
                    id="title"
                />
                <TextField
                    label="Content"
                    value={note.text}
                    onChange={(e) => setNote({...note, text: e.target.value})}
                    fullWidth
                    id="content"
                />
                <TextField
                    label="Date"
                    value={note.date}
                    InputProps={{
                        readOnly: true
                    }}
                    fullWidth
                    id="date"
                />
                <FormControl fullWidth>
                    <InputLabel>Grade</InputLabel>
                    <Select
                        value={grade}
                        onChange={handleGradeChange}
                        id="grade"
                    >
                        {options.map((option) => (
                            <MenuItem key={option.value} value={option.value}>
                                {option.label}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <div className="dialog-buttons">

                    <Button onClick={handleUpdate} variant="contained" color="primary" id="updateButton">
                        <FontAwesomeIcon icon={faSave}/> Update
                    </Button>

                    <Button onClick={onClose} variant="outlined" color="secondary" id="cancelButton">
                        <FontAwesomeIcon icon={faTimes}/> Cancel
                    </Button>


                </div>
            </div>
        </Dialog>
    );
};

export default UpdateNoteDialog;