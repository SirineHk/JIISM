import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { JiismService } from '../../../services/jiism.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-editor-articles',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './editor-articles.component.html'
})
export class EditorArticlesComponent {

  private service = inject(JiismService);
  private authService = inject(AuthService);

  articles: any[] = [];
  assignments: any[] = [];

  filter = 'ALL';
  search = '';

  selectedArticle: any = null;

  username = '';

  loading = true;
  error = '';

  stats = {
    total: 0,
    assigned: 0,
    review: 0,
    completed: 0
  };


  constructor() {

    this.username =
      this.authService.getUsername() || '';

    this.loadData();

  }


  private loadData(): void {

    this.loading = true;
    this.error = '';


    this.service.getAssignments().subscribe({

      next: (data) => {

        const allAssignments = data ?? [];


        /*
         * Garder uniquement les assignations
         * de l'éditeur connecté.
         */
        this.assignments =
          allAssignments.filter(
            assignment =>
              assignment.editor?.username === this.username
          );


        /*
         * Construire la liste des articles
         * à partir des assignations.
         */
        this.articles =
          this.assignments
            .filter(assignment => assignment.article)
            .map(assignment => ({
              ...assignment.article,

              assignmentId:
                assignment.id,

              assignmentStatus:
                assignment.status,

              assignedAt:
                assignment.assignedAt,

              dueDate:
                assignment.dueDate

            }));


        /*
         * Éviter les doublons d'articles.
         */
        const uniqueArticles =
          new Map<number, any>();


        this.articles.forEach(article => {

          if (!uniqueArticles.has(article.id)) {

            uniqueArticles.set(
              article.id,
              article
            );

          }

        });


        this.articles =
          Array.from(uniqueArticles.values());


        this.computeStats();

        this.loading = false;


        console.log(
          'Éditeur connecté :',
          this.username
        );

        console.log(
          'Articles de l’éditeur :',
          this.articles
        );

      },


      error: (err) => {

        console.error(
          'Erreur lors du chargement des articles :',
          err
        );

        this.articles = [];
        this.assignments = [];

        this.computeStats();

        this.error =
          'Impossible de charger vos articles.';

        this.loading = false;

      }

    });

  }


  private computeStats(): void {

    this.stats.total =
      this.articles.length;

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


  get filtered(): any[] {

    const searchValue =
      this.search
        .trim()
        .toLowerCase();


    return this.articles.filter(article => {

    const matchesStatus =
  this.filter === 'ALL' ||

  (
    ['ASSIGNED', 'IN_REVIEW', 'COMPLETED']
      .includes(this.filter) &&
    article.assignmentStatus === this.filter
  ) ||

  (
    ['ACCEPTED', 'REJECTED', 'PUBLISHED']
      .includes(this.filter) &&
    article.status === this.filter
  );


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


  label(status: string): string {

    const labels: Record<string, string> = {

      ASSIGNED: 'À traiter',

      IN_REVIEW: 'En révision',
      UNDER_REVIEW: 'En attente de décision',

      COMPLETED: 'Terminé',

      ACCEPTED: 'Accepté',

      REJECTED: 'Rejeté',
      PUBLISHED: 'Publié'

    };

    return labels[status] ?? status;

  }


  statusClass(status: string): string {

    const classes: Record<string, string> = {

      ASSIGNED:
        'bg-orange-100 text-orange-700',

      IN_REVIEW:
        'bg-violet-100 text-violet-700',

      COMPLETED:
        'bg-emerald-100 text-emerald-700',

    ACCEPTED:
      'bg-green-100 text-green-700',

    REJECTED:
      'bg-red-100 text-red-700',
    PUBLISHED:
    'bg-blue-100 text-blue-700'

    };

    return classes[status] ??
      'bg-slate-100 text-slate-600';

  }


  viewArticle(article: any): void {

    this.selectedArticle =
      article;

  }


  closeDetails(): void {

    this.selectedArticle =
      null;

  }


  openPdf(article: any): void {

    if (!article?.pdfUrl) {
      return;
    }


    let url =
      article.pdfUrl;


    if (url.startsWith('uploads/')) {

      url =
        url.replace(
          'uploads/',
          'files/'
        );

    }


    if (!url.startsWith('http')) {

      url =
        `http://jiism-backend-jiism.apps.dell64.dottn.com/${url}`;

    }


    window.open(
      url,
      '_blank',
      'noopener,noreferrer'
    );

  }


  startReview(article: any): void {
  if (!article?.assignmentId) {
    return;
  }

  this.service.startAssignment(article.assignmentId).subscribe({
    next: () => {
      article.assignmentStatus = 'IN_REVIEW';

      const assignment = this.assignments.find(
        a => a.id === article.assignmentId
      );

      if (assignment) {
        assignment.status = 'IN_REVIEW';
      }

      this.computeStats();

      console.log('Révision commencée pour l’article :', article.id);
    },
    error: (err) => {
      console.error('Erreur démarrage révision :', err);
      alert(
        err?.error || 'Impossible de commencer la révision.'
      );
    }
  });
}

completeReview(article: any): void {
  if (!article?.assignmentId) {
    return;
  }

  this.service.completeAssignment(article.assignmentId).subscribe({
    next: () => {
      article.assignmentStatus = 'COMPLETED';

      const assignment = this.assignments.find(
        a => a.id === article.assignmentId
      );

      if (assignment) {
        assignment.status = 'COMPLETED';
      }

      this.computeStats();

      console.log('Révision terminée pour l’article :', article.id);
    },
    error: (err) => {
      console.error('Erreur fin révision :', err);
      alert(
        err?.error || 'Impossible de terminer la révision.'
      );
    }
  });
}

acceptArticle(article: any): void {

  if (!article?.assignmentId) {
    return;
  }

  if (article.assignmentStatus !== 'COMPLETED') {
    return;
  }

  const confirmed = confirm(
    `Voulez-vous accepter l'article "${article.title}" ?`
  );

  if (!confirmed) {
    return;
  }

  this.service.acceptAssignment(article.assignmentId).subscribe({

    next: () => {

      // Le statut de l'article change
      article.status = 'ACCEPTED';

      // IMPORTANT :
      // assignmentStatus reste COMPLETED
      // car la révision est toujours terminée

      console.log(
        'Article accepté :',
        article.id
      );

      this.computeStats();

    },

    error: (err) => {

      console.error(
        'Erreur lors de l’acceptation :',
        err
      );

      alert(
        err?.error ||
        'Impossible d’accepter cet article.'
      );

    }

  });
}

rejectArticle(article: any): void {

  if (!article?.assignmentId) {
    return;
  }

  if (article.assignmentStatus !== 'COMPLETED') {
    return;
  }

  const confirmed = confirm(
    `Voulez-vous rejeter l'article "${article.title}" ?`
  );

  if (!confirmed) {
    return;
  }

  this.service.rejectAssignment(article.assignmentId).subscribe({

    next: () => {

      // Le statut de l'article change
      article.status = 'REJECTED';

      // IMPORTANT :
      // assignmentStatus reste COMPLETED

      console.log(
        'Article rejeté :',
        article.id
      );

      this.computeStats();

    },

    error: (err) => {

      console.error(
        'Erreur lors du rejet :',
        err
      );

      alert(
        err?.error ||
        'Impossible de rejeter cet article.'
      );

    }

  });
}

}
