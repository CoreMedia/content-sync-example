import Menu from "@jangaroo/ext-ts/menu/Menu";
import Config from "@jangaroo/runtime/Config";

interface PartialContentSyncMenuConfig extends Config<Menu> {
}

class PartialContentSyncMenu extends Menu {
  declare Config: PartialContentSyncMenuConfig;

  constructor(config: Config<PartialContentSyncMenu> = null) {
    super(config);
  }

  protected override afterRender(): void {
    super.afterRender();

  }
}

export default PartialContentSyncMenu;