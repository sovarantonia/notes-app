import React, {useState} from "react";
import {Button, Dialog, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup} from "@mui/material";
import {downloadNote} from "./api";

const DownloadDialog = ({open, onClose, noteId}) => {
    const [error, setError] = useState('');
    const [option, setOption] = useState('pdf');

    const handleChange = (e) => {
        setOption(e.target.value)
    }

    const handleDownloadNote = async () => {
        try {
            await downloadNote(noteId, option)
            setError('')
            onClose();
        } catch (error) {
            setError('Error downloading the note')
        }
    }

    return (
        <Dialog open={open} onClose={onClose}>
            <h2>Download note</h2>
            {error && <div className="error">{error}</div>}
            <FormControl>
                <FormLabel id="file-type">Download as:</FormLabel>
                <RadioGroup aria-labelledby="file-type" name="type" onChange={handleChange} value={option} row>
                    <FormControlLabel control={<Radio/>} label="Pdf" value="pdf"/>
                    <FormControlLabel control={<Radio/>} label="Document" value="docx"/>
                    <FormControlLabel control={<Radio/>} label="Text" value="txt"/>
                </RadioGroup>
            </FormControl>
            <Button onClick={handleDownloadNote}>
                Download
            </Button>
            <Button onClick={onClose}>
                Cancel
            </Button>
        </Dialog>
    );
};
export default DownloadDialog;