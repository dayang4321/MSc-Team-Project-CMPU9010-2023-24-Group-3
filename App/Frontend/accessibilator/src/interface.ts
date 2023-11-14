interface DocModifyParams {
  fontType:
    | 'openSans'
    | 'comicSans'
    | 'openDyslexic'
    | 'lexend'
    | 'arial'
    | 'helvetica';
  fontSize: number;
  lineSpacing: number;
  fontColor: string;
  characterSpacing: number;
  backgroundColor: string;
  alignment: 'LEFT' | 'RIGHT' | 'CENTRE';
  removeItalics: boolean;
}
