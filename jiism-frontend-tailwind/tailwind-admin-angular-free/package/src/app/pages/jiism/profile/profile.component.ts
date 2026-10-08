import {Component} from '@angular/core'; import {CommonModule} from '@angular/common'; import {FormsModule} from '@angular/forms';
@Component({selector:'app-profile',standalone:true,imports:[CommonModule,FormsModule],templateUrl:'./profile.component.html'}) export class ProfileComponent{firstName='Sirine';lastName='Kiri';email='sirine@jiism.tn';phone='';}
