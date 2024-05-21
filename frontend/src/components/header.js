import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import {Link, Menu, MenuItem} from "@mui/material";


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
        <Box sx={{ flexGrow: 1, background: "#DB7516" }}>
            <AppBar position="static">
                <Toolbar sx={{ display: 'flex', justifyContent: 'space-between', background: "#DB7516" }}>
                    {/* Menu icon on the left */}
                    <IconButton
                        size="large"
                        edge="start"
                        color="inherit"
                        aria-label="menu"
                        sx={{ mr: 2 }}
                        aria-controls={open ? 'basic-menu' : undefined}
                        aria-haspopup="true"
                        aria-expanded={open ? 'true' : undefined}
                        onClick={handleClick}
                    >
                        <MenuIcon />
                    </IconButton>

                    {/* Title text in the middle */}
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1, textAlign: 'center', fontFamily: "Papyrus", fontSize: 30 }}>
                       Share-notes-app
                    </Typography>
                    <Menu
                        id="basic-menu"
                        anchorEl={anchorEl}
                        open={open}
                        onClose={handleClose}
                        MenuListProps={{
                            'aria-labelledby': 'basic-button',
                        }}
                    >
                        <MenuItem component={Link} href="/" onClick={handleClose}>
                            Home
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
    );
}