import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-articles',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './articles.component.html'
})
export class ArticlesComponent {

  private service = inject(JiismService);

  articles: any[] = [];

  filter = 'ALL';

  search = '';

  selectedArticle: any = null;

  stats = {
    total: 0,
    pending: 0,
    review: 0,
    accepted: 0,
    rejected: 0,
    published: 0
  };


  constructor() {
    this.loadArticles();
  }


  /**
   * Chargement des articles depuis le backend
   */
  private loadArticles(): void {

  this.service.getArticles().subscribe({

    next: (articles) => {

      console.log('ARTICLES REÇUS :', articles);

      this.articles = articles ?? [];

      console.log(
        'ARTICLE 7 :',
        this.articles.find(a => a.id === 7)
      );

      console.log(
        'AUTEUR ARTICLE 7 :',
        this.articles.find(a => a.id === 7)?.submittedBy?.username
      );

      this.computeStats();

    },

    error: (error) => {

      console.error(
        'Erreur lors du chargement des articles :',
        error
      );

      this.articles = [];

      this.computeStats();

    }

  });

}


  /**
   * Statistiques des articles
   */
  private computeStats(): void {

    this.stats.total = this.articles.length;

    this.stats.pending =
      this.articles.filter(a => a.status === 'PENDING').length;

    this.stats.review =
      this.articles.filter(a => a.status === 'UNDER_REVIEW').length;

    this.stats.accepted =
      this.articles.filter(a => a.status === 'ACCEPTED').length;

    this.stats.rejected =
      this.articles.filter(a => a.status === 'REJECTED').length;

    this.stats.published =
      this.articles.filter(a => a.status === 'PUBLISHED').length;

  }


  /**
   * Articles correspondant au filtre + recherche
   */
  get filtered(): any[] {

    const searchValue = this.search
      .trim()
      .toLowerCase();


    return this.articles.filter(article => {

      const matchesStatus =
        this.filter === 'ALL' ||
        article.status === this.filter;


      const matchesSearch =
        !searchValue ||
        (article.title ?? '')
          .toLowerCase()
          .includes(searchValue) ||
        (article.authors ?? '')
          .toLowerCase()
          .includes(searchValue);


      return matchesStatus && matchesSearch;

    });

  }


  /**
   * Libellé français du statut
   */
  label(status: string): string {

    const labels: Record<string, string> = {

      PENDING: 'En attente',

      UNDER_REVIEW: 'En révision',

      ACCEPTED: 'Accepté',

      REJECTED: 'Rejeté',

      PUBLISHED: 'Publié'

    };

    return labels[status] ?? status;

  }


  /**
   * Style du badge selon le statut
   */
  statusClass(status: string): string {

    const classes: Record<string, string> = {

      PENDING:
        'bg-orange-100 text-orange-700',

      UNDER_REVIEW:
        'bg-violet-100 text-violet-700',

      ACCEPTED:
        'bg-blue-100 text-blue-700',

      REJECTED:
        'bg-red-100 text-red-700',

      PUBLISHED:
        'bg-emerald-100 text-emerald-700'

    };

    return classes[status] ??
      'bg-slate-100 text-slate-600';

  }


  /**
   * Afficher le détail d'un article
   */
  viewArticle(article: any): void {

    this.selectedArticle = article;

  }


  /**
   * Fermer le détail
   */
  closeDetails(): void {

    this.selectedArticle = null;

  }


  /**
   * Ouvrir le PDF
   */
  openPdf(article: any): void {

  if (!article?.pdfUrl) {
    return;
  }

  let url = article.pdfUrl;

  // uploads/xxx.pdf → files/xxx.pdf
  if (url.startsWith('uploads/')) {
    url = url.replace('uploads/', 'files/');
  }

  // Ajouter l'URL du backend
  if (!url.startsWith('http')) {
    url = `http://jiism-backend-jiism.apps.dell64.dottn.com/${url}`;
  }

  window.open(
    url,
    '_blank',
    'noopener,noreferrer'
  );
}


  /**
   * Publication
   *
   * Pour l'instant nous ne faisons pas d'appel backend ici.
   * Le bouton est préparé pour être relié à l'endpoint
   * de publication lorsque celui-ci sera confirmé.
   */
 publishArticle(article: any): void {

  if (!article?.id) {
    return;
  }

  if (article.status !== 'ACCEPTED') {
    return;
  }

  const confirmed = window.confirm(
    `Voulez-vous publier l'article "${article.title}" ?`
  );

  if (!confirmed) {
    return;
  }

  this.service.publishArticle(article.id).subscribe({

    next: (updatedArticle) => {

      console.log(
        'Article publié avec succès :',
        updatedArticle
      );

      const index = this.articles.findIndex(
        a => a.id === article.id
      );

      if (index !== -1) {
        this.articles[index] = updatedArticle;
      }

      this.computeStats();

      if (
        this.selectedArticle &&
        this.selectedArticle.id === article.id
      ) {
        this.selectedArticle = updatedArticle;
      }

      alert('Article publié avec succès.');

    },

    error: (error) => {

      console.error(
        'Erreur lors de la publication :',
        error
      );

      const message =
        typeof error?.error === 'string'
          ? error.error
          : 'Impossible de publier l’article.';

      alert(message);

    }

  });

}

}