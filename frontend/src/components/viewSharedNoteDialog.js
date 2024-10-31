import React, {useEffect, useState} from "react";
import {getNoteById} from "./api";
import {Button, Dialog, DialogTitle, TextField} from "@mui/material";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";

const SharedNoteDialog = ({open, onClose, noteId, dialogType}) => {
    const [note, setNote] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        if (open && noteId) {
            const fetchNote = async () => {
                try {
                    const data = await getNoteById(noteId);
                    setNote(data);
                } catch (err) {
                    setError('Failed to fetch note');
                }
            };

            fetchNote();
        }
    }, [open, noteId]);

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" className="update-dialog">
            <div className="dialog-content">
                <DialogTitle>{dialogType === "view-received-note" ? "Received Note" : "Sent Note"}</DialogTitle>
                {error && <div className="error">{error}</div>}

                {note && (
                    <>
                        <TextField
                            label="Title"
                            value={note.title}
                            aria-readonly={true}
                            fullWidth
                            id="title"
                        />
                        <TextField
                            label="Content"
                            value={note.text}
                            aria-readonly={true}
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
                        <TextField
                            label="Grade"
                            value={note.grade}
                            InputProps={{
                                readOnly: true
                            }}
                            fullWidth
                            id="grade"
                        />
                    </>
                )}

                <div className="dialog-buttons">
                    <Button onClick={onClose} variant="contained" color="primary" id="closeButton">
                        <FontAwesomeIcon icon={faTimes}/> Close
                    </Button>
                </div>
            </div>
        </Dialog>
    );
};

export default SharedNoteDialog;
