import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
//const { shell } = require('electron')

class ScrollDialog extends React.Component {
  state = {
    scroll: 'paper',
  };

  handleClose = () => {
    this.props.handleClose();
  };

  openGithub = () => {
    //shell.openExternal('https://github.com/vontell/Bility')
  }

  render() {
    return (
      <div>
        <Dialog
          open={this.props.open}
          onClose={this.handleClose}
          scroll={this.state.scroll}
          aria-labelledby="scroll-dialog-title"
        >
          <DialogTitle id="scroll-dialog-title">About Bility</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Bility is a testing framework for reporting accessibility and usability
              issues within Android applications. Simply drag in an Android project,
              click start, and view accessibility results in real time.<br />
              <br />
              Want to contribute? Visit our GitHub and website!
              <br />
              <Button onClick={this.openGithub}>GitHub Repository</Button>
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={this.handleClose} color="primary">
              Close
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    );
  }
}

export default ScrollDialog;