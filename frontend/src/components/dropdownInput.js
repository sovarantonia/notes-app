import React, {useState} from 'react';
import {Autocomplete, TextField} from '@mui/material';
import '../resources/dropdown-input.css'

const DropdownInput = ({options, onSearch, onSelect}) => {
    const [inputValue, setInputValue] = useState('');

    const mappedOptions = options.map(option => ({
        label: `${option.firstName} ${option.lastName} (${option.email})`,
        user: option,
    }));

    const handleInputChange = (event, newInputValue) => {
        setInputValue(newInputValue);
        onSearch(newInputValue);
    };

    const handleSelect = (e, newValue) => {
      if (newValue){
          onSelect(newValue.user);
      }
    }

    return (
        <div className="dropdownInputContainer">
            <Autocomplete
                id="searchUser"
                options={mappedOptions}
                getOptionLabel={(option) => option.label}
                freeSolo
                inputValue={inputValue}
                onChange={handleSelect}
                onInputChange={handleInputChange}
                ListboxProps={{
                    style: {
                        maxHeight: '95px',
                        overflow: 'auto',
                    },
                }}
                renderInput={(params) => (
                    <TextField
                        id="searchUserTextField"
                        {...params}
                        label="Search user"
                        sx={{ backgroundColor: "white", borderRadius: '7px' }}
                    />
                )}
            />
        </div>
    );
};

export default DropdownInput;