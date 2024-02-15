import jenkins.model.*
import hudson.model.ModelObject
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import org.jenkinsci.plugins.plaincredentials.StringCredentials
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl
import com.cloudbees.hudson.plugins.folder.Folder


class DeepCredentialsPrinter {

  private static final boolean DEBUG = false;

  private final out;
  private final Set<CredentialsStore> visitedStores = new HashSet<>();

  DeepCredentialsPrinter(out) {
      this.out = out;
  }

  private void start() {
    process(Jenkins.getInstance())
  }

  private void process(ItemGroup group) {
    printCreds(group);

    List<ItemGroup> items = group.getItems();
    if (items == null || items.isEmpty()) {
      return;
    }

    for (item in items) {
      if (item instanceof ItemGroup) {
        process(item);
      } else if (item instanceof Item) {
        printCreds(item)
      } else {
        if (DEBUG) {
          out.println("[DEBUG] unsupported item type: " + item.getClass().getCanonicalName());
        }
      }
    }
  }

  private void printCreds(ModelObject model) {
    for (store in CredentialsProvider.lookupStores(model)) {
      if (visitedStores.add(store)) { // only visit new stores
        print(model.getFullName(), store.getCredentials(Domain.global()));
      }
    }
  }

  private void print(String fullname, List<Credentials> creds) {
    if (creds.isEmpty()) {
      if (DEBUG) {
        out.println("[DEBUG] No credentials in /" + fullname);
      }
    } else {
      for (c in creds) {
        out.println("Folder: /" + fullname)
        out.println("   id: " + c.id)
        if (c.properties.description) {
            out.println("   description: " + c.description)
        }
        if (c.properties.username) {
            out.println("   username: " + c.username)
        }
        if (c.properties.password) {
            out.println("   password: " + c.password)
        }
        if (c.properties.password) {
            out.println("   password: " + c.password)
        }
        if (c.properties.passphrase) {
            out.println("   passphrase: " + c.passphrase)
        }
        if (c.properties.secret) {
            out.println("   secret: " + c.secret)
        }
        if (c.properties.accessKey) {
            out.println("   accessKey: " + c.accessKey)
        }
           if (c.properties.secretKey) {
            out.println("   secretKey: " + c.secretKey)
        }
        if (c.properties.secretBytes) {
            out.println("    secretBytes: ")
            out.println("\n" + new String(c.secretBytes.getPlainData()))
        }
        if (c.properties.privateKeySource) {
            out.println("   privateKey: " + c.getPrivateKey())
        }
        if (c.properties.apiToken) {
            out.println("   apiToken: " + c.apiToken)
        }
        if (c.properties.token) {
            out.println("   token: " + c.token)
        }
        out.println("")
        }
    }
  }
}

new DeepCredentialsPrinter(getBinding().out).start();
