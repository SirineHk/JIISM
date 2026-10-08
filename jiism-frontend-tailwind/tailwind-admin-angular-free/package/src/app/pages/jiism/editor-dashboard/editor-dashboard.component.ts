import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { JiismService } from '../../../services/jiism.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-editor-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './editor-dashboard.component.html'
})
export class EditorDashboardComponent {

  private service = inject(JiismService);
  private authService = inject(AuthService);

  articles: any[] = [];
  assignments: any[] = [];
  announcements: any[] = [];

  username = '';

  stats = {
    total: 0,
    assigned: 0,
    review: 0,
    completed: 0
  };

  loading = true;
  error = '';

  constructor() {
    this.username = this.authService.getUsername() || '';
    this.loadDashboard();
  }

  private loadDashboard(): void {

    this.loading = true;
    this.error = '';

    // Articles
    this.service.getArticles().subscribe({

      next: (articles) => {

        this.articles = articles ?? [];

      },

      error: (err) => {

        console.error(
          'Erreur lors du chargement des articles :',
          err
        );

        this.articles = [];

      }

    });


    // Assignations de l'éditeur connecté
    this.service.getAssignments().subscribe({

      next: (data) => {

        const allAssignments = data ?? [];

        this.assignments = allAssignments.filter(
          assignment =>
            assignment.editor?.username === this.username
        );

        this.computeAssignmentStats();

        console.log(
          'Dashboard éditeur - utilisateur :',
          this.username
        );

        console.log(
          'Dashboard éditeur - assignations :',
          this.assignments
        );

      },

      error: (err) => {

        console.error(
          'Erreur lors du chargement des assignations :',
          err
        );

        this.assignments = [];

        this.computeAssignmentStats();

      }

    });


    // Annonces
    this.service.getPublicAnnouncements().subscribe({

      next: (announcements) => {

        this.announcements = announcements ?? [];
        this.loading = false;

      },

      error: (err) => {

        console.error(
          'Erreur lors du chargement des annonces :',
          err
        );

        this.announcements = [];

        this.loading = false;

      }

    });

  }


  private computeAssignmentStats(): void {

    this.stats.total = this.assignments.length;

    this.stats.assigned =
      this.assignments.filter(
        assignment =>
          assignment.status === 'ASSIGNED'
      ).length;

    this.stats.review =
      this.assignments.filter(
        assignment =>
          assignment.status === 'IN_REVIEW'
      ).length;

    this.stats.completed =
      this.assignments.filter(
        assignment =>
          assignment.status === 'COMPLETED'
      ).length;

  }


  label(status: string): string {

    const labels: Record<string, string> = {

      ASSIGNED: 'À traiter',

      IN_REVIEW: 'En révision',

      COMPLETED: 'Terminé',

      PENDING: 'En attente',

      UNDER_REVIEW: 'En révision',

      ACCEPTED: 'Accepté',

      REJECTED: 'Rejeté',

      PUBLISHED: 'Publié'

    };

    return labels[status] || status;

  }

}
