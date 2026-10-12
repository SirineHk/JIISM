import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { JiismService } from '../../services/jiism.service';

@Component({
  selector: 'app-author-articles',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './author-articles.component.html'
})
export class AuthorArticlesComponent implements OnInit {

  private jiismService = inject(JiismService);

  articles: any[] = [];

  selectedArticle: any = null;
    showDetails = false;

  total = 0;
  pending = 0;
  underReview = 0;
  accepted = 0;
  rejected = 0;
  published = 0;

  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadMyArticles();
  }

  // ==========================================
  // CHARGER MES ARTICLES
  // ==========================================

  loadMyArticles(): void {

    this.loading = true;
    this.errorMessage = '';

    this.jiismService.getMyArticles().subscribe({
      next: (data) => {

        this.articles = data || [];

        this.calculateStats();

        this.loading = false;
      },

      error: (error) => {

        console.error(
          'Erreur chargement articles auteur :',
          error
        );

        this.errorMessage =
          'Impossible de charger vos articles.';

        this.loading = false;
      }
    });
  }

  // ==========================================
  // STATISTIQUES
  // ==========================================

  calculateStats(): void {

    this.total = this.articles.length;

    this.pending = this.articles.filter(
      article => article.status === 'PENDING'
    ).length;

    this.underReview = this.articles.filter(
      article => article.status === 'UNDER_REVIEW'
    ).length;

    this.accepted = this.articles.filter(
      article => article.status === 'ACCEPTED'
    ).length;

    this.rejected = this.articles.filter(
      article => article.status === 'REJECTED'
    ).length;

    this.published = this.articles.filter(
      article => article.status === 'PUBLISHED'
    ).length;
  }

  // ==========================================
  // LABEL STATUT
  // ==========================================

  getStatusLabel(status: string): string {

    switch (status) {

      case 'PENDING':
        return 'En attente';

      case 'UNDER_REVIEW':
        return 'En révision';

      case 'ACCEPTED':
        return 'Accepté';

      case 'REJECTED':
        return 'Rejeté';

      case 'PUBLISHED':
        return 'Publié';

      default:
        return status;
    }
  }

  // ==========================================
  // CLASSE STATUT
  // ==========================================

  getStatusClass(status: string): string {

    switch (status) {

      case 'PENDING':
        return 'bg-yellow-100 text-yellow-700';

      case 'UNDER_REVIEW':
        return 'bg-blue-100 text-blue-700';

      case 'ACCEPTED':
        return 'bg-green-100 text-green-700';

      case 'REJECTED':
        return 'bg-red-100 text-red-700';

      case 'PUBLISHED':
        return 'bg-purple-100 text-purple-700';

      default:
        return 'bg-gray-100 text-gray-700';
    }
  }

  // ==========================================
  // FORMAT DATE
  // ==========================================

  formatDate(date: string | null): string {

    if (!date) {
      return '-';
    }

    return new Date(date).toLocaleDateString(
      'fr-FR'
    );
  }

  // ==========================================
  // OUVRIR PDF
  // ==========================================

  openPdf(article: any): void {

    if (!article.pdfUrl) {
      alert('Aucun PDF disponible pour cet article.');
      return;
    }

    let pdfPath = article.pdfUrl;

    if (pdfPath.startsWith('uploads/')) {
      pdfPath = pdfPath.replace(
        'uploads/',
        'files/'
      );
    }

    const url =
      `http://jiism-backend-jiism.apps.dell64.dottn.com/${pdfPath}`;

    window.open(url, '_blank');
  }
  // ==========================================
// AFFICHER LES DÉTAILS
// ==========================================

openDetails(article: any): void {

  this.selectedArticle = article;
  this.showDetails = true;

}

// ==========================================
// FERMER LES DÉTAILS
// ==========================================

closeDetails(): void {

  this.showDetails = false;
  this.selectedArticle = null;

}
}