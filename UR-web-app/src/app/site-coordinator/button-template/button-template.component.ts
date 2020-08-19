import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-button-template',
  templateUrl: './button-template.component.html',
  styleUrls: ['./button-template.component.scss'],
})
export class ButtonTemplateComponent {
  @Input()
  buttonName = '';
  @Output() textBtnClickEmt: EventEmitter<string> = new EventEmitter<string>();

  onBtnClick() {
    this.textBtnClickEmt.emit('You have clicked on button.');
  }
}
