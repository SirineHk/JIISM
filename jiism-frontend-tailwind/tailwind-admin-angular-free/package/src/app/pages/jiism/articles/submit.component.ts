import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

import { JiismService } from '../../../services/jiism.service';

@Component({
  selector: 'app-submit-article',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './submit.component.html'
})
export class SubmitArticleComponent {

  private jiismService = inject(JiismService);
  private router = inject(Router);

  // ==========================================
  // FORMULAIRE
  // ==========================================

  title = '';
  abstractText = '';
  keywords = '';
  authors = '';
  affiliations = '';

  pdfUrl = '';

  selectedFile: File | null = null;

  // ==========================================
  // ÉTAT
  // ==========================================

  submitted = false;
  loading = false;

  errorMessage = '';

  // ==========================================
  // SÉLECTION DU PDF
  // ==========================================

  onFileSelected(event: Event): void {

    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      this.selectedFile = null;
      return;
    }

    const file = input.files[0];

    // Vérification PDF
    if (
      file.type !== 'application/pdf' &&
      !file.name.toLowerCase().endsWith('.pdf')
    ) {
      this.errorMessage =
        'Veuillez sélectionner un fichier PDF.';

      this.selectedFile = null;
      return;
    }

    // Taille maximale : 10 Mo
    if (file.size > 10 * 1024 * 1024) {

      this.errorMessage =
        'Le fichier PDF ne doit pas dépasser 10 Mo.';

      this.selectedFile = null;
      return;
    }

    this.errorMessage = '';
    this.selectedFile = file;
  }

  // ==========================================
  // SOUMISSION
  // ==========================================

  submit(): void {

    this.errorMessage = '';

    // Vérification titre
    if (!this.title.trim()) {

      this.errorMessage =
        'Le titre de l’article est obligatoire.';

      return;
    }

    // Vérification PDF
    if (!this.selectedFile) {

      this.errorMessage =
        'Veuillez sélectionner le fichier PDF de l’article.';

      return;
    }

    this.loading = true;

    // ========================================
    // ÉTAPE 1 : UPLOAD PDF
    // ========================================

    this.jiismService
      .uploadPdf(this.selectedFile)
      .subscribe({

        next: (pdfPath: string) => {

          console.log(
            'PDF uploadé :',
            pdfPath
          );

          this.pdfUrl = pdfPath;

          // ==================================
          // ÉTAPE 2 : CRÉATION ARTICLE
          // ==================================

          this.createArticle();
        },

        error: (error) => {

          console.error(
            'Erreur upload PDF :',
            error
          );

          this.loading = false;

          this.errorMessage =
            error?.error ||
            'Erreur lors de l’envoi du fichier PDF.';
        }

      });
  }

  // ==========================================
  // CRÉER L'ARTICLE
  // ==========================================

  private createArticle(): void {

    const article = {

      title: this.title.trim(),

      abstractText:
        this.abstractText.trim(),

      keywords:
        this.keywords.trim(),

      authors:
        this.authors.trim(),

      affiliations:
        this.affiliations.trim(),

      pdfUrl:
        this.pdfUrl

    };

    console.log(
      'Article envoyé :',
      article
    );

    this.jiismService
      .createArticle(article)
      .subscribe({

        next: (response) => {

          console.log(
            'Article créé :',
            response
          );

          this.loading = false;
          this.submitted = true;

          // Redirection vers "Mes articles"
          setTimeout(() => {

            this.router.navigate([
              '/author/articles'
            ]);

          }, 1500);
        },

        error: (error) => {

          console.error(
            'Erreur création article :',
            error
          );

          this.loading = false;

          this.errorMessage =
            error?.error ||
            'Erreur lors de la création de l’article.';
        }

      });
  }
}