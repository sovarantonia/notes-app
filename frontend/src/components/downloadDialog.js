import React, {useState} from "react";
import {Button, Dialog, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup} from "@mui/material";
import {downloadNote} from "./api";
import '../resources/download-dialog.css';
import {faCircleDown, faTimes} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

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
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" className="download-dialog">
            <div className="dialog-content">
                <h2>Download Note</h2>
                {error && <div className="error">{error}</div>}
                <FormControl>
                    <FormLabel id="file-type" shrink={true}>Download as:</FormLabel>
                    <RadioGroup
                        aria-labelledby="file-type"
                        name="type"
                        onChange={handleChange}
                        value={option}
                        row
                    >
                        <FormControlLabel control={<Radio/>} label="Pdf" value="pdf"/>
                        <FormControlLabel control={<Radio/>} label="Document" value="docx"/>
                        <FormControlLabel control={<Radio/>} label="Text" value="txt"/>
                    </RadioGroup>
                </FormControl>
                <div className="dialog-buttons">
                    <Button id="downloadButton" onClick={handleDownloadNote} variant="contained">
                        <FontAwesomeIcon icon={faCircleDown}/>Download
                    </Button>
                    <Button id="cancelButton" onClick={onClose} variant="outlined" color="secondary">
                        <FontAwesomeIcon icon={faTimes}/> Cancel
                    </Button>
                </div>
            </div>
        </Dialog>
    );
};
export default DownloadDialog;