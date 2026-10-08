import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
@Component({selector:'app-submit-article',standalone:true,imports:[CommonModule,FormsModule,RouterModule],templateUrl:'./submit.component.html'})
export class SubmitArticleComponent { title=''; abstractText=''; keywords=''; authors=''; affiliations=''; pdfUrl=''; submitted=false; submit(){this.submitted=true;} }
