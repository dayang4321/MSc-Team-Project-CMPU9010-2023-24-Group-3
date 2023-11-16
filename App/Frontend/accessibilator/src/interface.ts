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
  generateTOC: boolean;
}
interface DocumentData {
  documentID: string;
  documentKey: string;
  documentConfig: DocModifyParams;
  versions: {
    originalVersion: {
      url: string;
      versionID: string;
    };
    currentVersion: {
      url: string;
      versionID: string;
    };
  };
}
