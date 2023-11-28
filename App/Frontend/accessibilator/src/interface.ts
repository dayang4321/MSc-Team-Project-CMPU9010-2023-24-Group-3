interface DocModifyParams {
  fontType:
    | 'openSans'
    | 'comicSans'
    | 'openDyslexic'
    | 'lexend'
    | 'arial'
    | 'helvetica'
    | null;
  fontSize: number | null;
  lineSpacing: number | null;
  fontColor: string | null;
  characterSpacing: number | null;
  backgroundColor: string | null;
  alignment: 'LEFT' | 'RIGHT' | 'CENTRE' | null;
  removeItalics: boolean | null;
  generateTOC: boolean | null;
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

interface User {
  userId: string;
  email: string;
  username: string;
}
