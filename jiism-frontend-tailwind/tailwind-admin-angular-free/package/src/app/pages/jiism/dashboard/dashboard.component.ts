import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-jiism-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './dashboard.component.html'
})
export class JiismDashboardComponent {

  private service = inject(JiismService);

  // ==============================
  // Données du dashboard
  // ==============================

  articles: any[] = [];
  assignments: any[] = [];
  announcements: any[] = [];

  stats = {
    total: 0,
    pending: 0,
    review: 0,
    published: 0
  };

  loading = true;
  error = '';

  // ==============================
  // Initialisation
  // ==============================

  constructor() {
    this.loadDashboard();
  }

  // ==============================
  // Chargement des données
  // ==============================

  private loadDashboard(): void {

    this.loading = true;
    this.error = '';

    // Articles
    this.service.getArticles().subscribe({
      next: (articles) => {
        this.articles = articles ?? [];
        this.computeStats();
      },

      error: (err) => {
        console.error('Erreur lors du chargement des articles:', err);
        this.error = 'Impossible de charger les articles.';
        this.articles = [];
        this.computeStats();
      }
    });

    // Assignations
    this.service.getAssignments().subscribe({
      next: (assignments) => {
        this.assignments = assignments ?? [];
      },

      error: (err) => {
        console.error('Erreur lors du chargement des assignations:', err);
        this.assignments = [];
      }
    });

    // Annonces
    this.service.getAnnouncements().subscribe({
      next: (announcements) => {
        this.announcements = announcements ?? [];
        this.loading = false;
      },

      error: (err) => {
        console.error('Erreur lors du chargement des annonces:', err);
        this.announcements = [];
        this.loading = false;
      }
    });
  }

  // ==============================
  // Calcul des statistiques
  // ==============================

  private computeStats(): void {

    this.stats.total = this.articles.length;

    this.stats.pending =
      this.articles.filter(
        article => article.status === 'PENDING'
      ).length;

    this.stats.review =
      this.articles.filter(
        article => article.status === 'UNDER_REVIEW'
      ).length;

    this.stats.published =
      this.articles.filter(
        article => article.status === 'PUBLISHED'
      ).length;
  }

  // ==============================
  // Traduction des statuts
  // ==============================

  label(status: string): string {

    const labels: Record<string, string> = {
      PENDING: 'En attente',
      UNDER_REVIEW: 'En révision',
      ACCEPTED: 'Accepté',
      REJECTED: 'Rejeté',
      PUBLISHED: 'Publié'
    };

    return labels[status] || status;
  }
}
