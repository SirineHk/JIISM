import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-public',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './public.component.html'
})
export class PublicComponent {

  service = inject(JiismService);

  articles: any[] = [];

  loading = true;
  error = false;

  constructor() {
    this.loadArticles();
  }

  loadArticles() {

    this.loading = true;
    this.error = false;

    this.service.getPublicArticles().subscribe({

      next: data => {

        console.log('Articles publics reçus :', data);

        this.articles = data ?? [];

        this.loading = false;
      },

      error: error => {

        console.error(
          'Erreur récupération articles publics :',
          error
        );

        this.error = true;
        this.loading = false;
      }

    });
  }
}