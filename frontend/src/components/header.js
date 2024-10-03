import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import {Link, Menu, MenuItem} from "@mui/material";
import '../resources/register-page.css';

export default function ButtonAppBar() {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };
    return (
        <div className={"headerContainer"}  style={{ backgroundColor: "#D8698E" }}>
            <Box>
                <AppBar>
                    <Toolbar>
                        {/* Menu icon on the left */}
                        <IconButton onClick={handleClick}>
                            <MenuIcon/>
                        </IconButton>

                        {/* Title text in the middle */}
                        <Typography>Share-notes-app</Typography>
                        <Menu
                            id="basic-menu"
                            anchorEl={anchorEl}
                            open={open}
                            onClose={handleClose}

                        >
                            <MenuItem component={Link} href="/" onClick={handleClose}>
                                Home
                            </MenuItem>

                            <MenuItem component={Link} href="/About" onClick={handleClose}>
                                About
                            </MenuItem>

                            <MenuItem component={Link} href="/Login" onClick={handleClose}>
                                Login
                            </MenuItem>

                            <MenuItem component={Link} href="/Register" onClick={handleClose}>
                                Register
                            </MenuItem>



                        </Menu>

                    </Toolbar>
                </AppBar>
            </Box>
        </div>
    );
}